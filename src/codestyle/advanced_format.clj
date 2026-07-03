(ns codestyle.advanced-format
  (:require [codestyle.common :as common]
            [zprint.core :as zprint]))

(def default-config-resource "codestyle/zprint.edn")

(defn- load-config
  [explicit-config-path]
  (let [project-config-path
        (common/project-config-path ".zprint.edn")

        effective-config-path
        (or explicit-config-path project-config-path)

        config-string
        (if effective-config-path
          (slurp effective-config-path)
          (common/resource-string default-config-resource))]

    ;; ponytail: eval trusts the config file. Fine for project-local
    ;; formatter configs; do not point --config at hostile input.
    (eval (read-string config-string))))

(defn- format-string [config file-path source] (zprint/zprint-file-str source file-path config))

(defn- run-check!
  [config files]
  (let [failed-files (reduce (fn [acc file-path]
                               (let [original (slurp file-path)
                                     formatted (format-string config file-path original)]

                                 (if (= original formatted)
                                   acc
                                   (do (binding [*out* *err*]
                                         (println (str "Would reformat: " file-path)))
                                       (conj acc file-path)))))
                             []
                             files)]
    (if (seq failed-files)
      (do (binding [*out* *err*]
            (println)
            (println (str (count failed-files)
                          " file(s) need formatting. Run codestyle.advanced-format fix.")))
          (System/exit 1))
      (println "All files are formatted."))))

(defn- run-fix!
  [config files]
  (doseq [file-path files]
    (let [original (slurp file-path)
          formatted (format-string config file-path original)]

      (when-not (= original formatted) (spit file-path formatted))))
  (println (str "Formatted " (count files) " file(s).")))

(defn -main
  [& args]
  (let [{:keys [mode config-path paths]}
        (common/parse-format-args args)

        resolved-files
        (common/expand-clojure-files paths)

        effective-config
        (load-config config-path)]

    (if (empty? resolved-files)
      (println "No Clojure files found.")
      (if (= mode :check)
        (run-check! effective-config resolved-files)
        (run-fix! effective-config resolved-files)))))
