(ns codestyle.lint
  (:require [clj-kondo.main :as clj-kondo]
            [codestyle.common :as common]))

(defn -main
  [& paths]
  (let [resolved-paths (common/resolve-target-paths paths)]
    (if (empty? resolved-paths)
      (println "No target paths found.")
      (apply clj-kondo/-main (into ["--lint"] resolved-paths)))))
