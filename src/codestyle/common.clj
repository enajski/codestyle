(ns codestyle.common
  (:require [clojure.java.io :as jio]
            [clojure.string :as str]))

(def default-target-paths ["src" "test" "api" "api/src" "src/main" "src/test" "src/devcards" "dev"])

(defn fail!
  [message]
  (binding [*out* *err*]
    (println message))
  (System/exit 1))

(defn project-config-path
  [file-name]
  (let [file (jio/file file-name)]
    (when (.exists file) (.getPath file))))

(defn resource-string
  [resource-path]
  (let [resource (jio/resource resource-path)]
    (when-not resource (fail! (str "Missing resource: " resource-path)))
    (slurp resource)))

(defn clojure-source-file?
  [file]
  (let [name (.getName file)]
    (or (str/ends-with? name ".clj") (str/ends-with? name ".cljc") (str/ends-with? name ".cljs"))))

(defn resolve-target-paths
  [paths]
  (let [requested-paths
        (if (seq paths) paths default-target-paths)

        resolved-paths
        (reduce (fn [acc path]
                  (let [file (jio/file path)]
                    (if (.exists file) (conj acc path) acc)))
                []
                requested-paths)]

    resolved-paths))

(defn expand-clojure-files
  [paths]
  (let [resolved-paths
        (resolve-target-paths paths)

        collected-files
        (reduce (fn [acc path]
                  (let [file (jio/file path)]
                    (cond (and (.isFile file) (clojure-source-file? file)) (conj acc
                                                                                 (.getPath file))
                          (.isDirectory file) (let [directory-files
                                                    (reduce (fn [dir-acc child]
                                                              (if (and (.isFile child)
                                                                       (clojure-source-file? child))
                                                                (conj dir-acc (.getPath child))
                                                                dir-acc))
                                                            []
                                                            (file-seq file))]
                                                (into acc directory-files))
                          :else acc)))
                []
                resolved-paths)

        unique-files
        (distinct collected-files)]

    (vec (sort unique-files))))

(defn parse-format-args
  [args]
  (loop [remaining-args
         args

         mode
         nil

         config-path
         nil

         paths
         []]

    (if (empty? remaining-args)
      {:mode (or mode :fix) :config-path config-path :paths paths}
      (let [current-arg
            (first remaining-args)

            next-args
            (rest remaining-args)]

        (cond (= current-arg "--check") (recur next-args :check config-path paths)
              (#{"check" "fix"} current-arg)
              (recur next-args (keyword current-arg) config-path paths)
              (= current-arg "--config") (let [next-config-path (first next-args)]
                                           (when-not next-config-path
                                             (fail! "Missing value after --config"))
                                           (recur (rest next-args) mode next-config-path paths))
              :else (recur next-args mode config-path (conj paths current-arg)))))))
