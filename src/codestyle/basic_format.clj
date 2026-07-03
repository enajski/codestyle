(ns codestyle.basic-format
  (:require [cljfmt.main :as cljfmt]
            [codestyle.common :as common]))

(defn- cljfmt-args
  [{:keys [mode config-path]} paths]
  (let [base-args
        (into [(name mode)] paths)

        configured-args
        (if config-path (into base-args ["--config" config-path]) base-args)]

    configured-args))

(defn -main
  [& args]
  (let [{:keys [config-path] :as parsed-args}
        (common/parse-format-args args)

        resolved-paths
        (common/resolve-target-paths (:paths parsed-args))

        effective-config-path
        (or config-path (common/project-config-path "cljfmt.edn"))

        final-args
        (cljfmt-args (assoc parsed-args :config-path effective-config-path) resolved-paths)]

    (if (empty? resolved-paths)
      (println "No target paths found.")
      (apply cljfmt/-main final-args))))
