(ns codestyle.zprint-test
  (:require [clojure.test :refer [deftest is run-tests testing]]
            [zprint.core :as zprint]))

(def bundled-config (eval (read-string (slurp "resources/codestyle/zprint.edn"))))

(defn format-source [source] (zprint/zprint-file-str source "sample.clj" bundled-config))

(deftest cond-threading-guides
  (testing "cond-> keeps its init expression and clause pairs aligned"
    (is (= "(cond-> x
  a
  (f)

  b
  (#(g %)))
"
           (format-source "(cond-> x
  a (f)
  b (#(g %)))
"))))
  (testing "cond->> keeps its init expression and clause pairs aligned"
    (is (= "(cond->> xs
  a
  (map inc)

  b
  (#(filter odd? %)))
"
           (format-source "(cond->> xs
  a (map inc)
  b (#(filter odd? %)))
"))))
  (testing "recursively-descended cond-> forms without an init slot still count pairs"
    (is (= "(cond->
  a
  (f)

  b
  (#(g %)))
"
           (format-source "(cond->
  a (f)
  b (#(g %)))
")))))

(deftest binding-form-guides
  (testing "if-let uses the single-binding compact layout"
    (is (= "(if-let [x (lookup m)]
  (use x)
  (fallback))
"
           (format-source "(if-let [x (lookup m)] (use x) (fallback))
"))))
  (testing "when-let flows multiple bindings with a blank line before the body"
    (is (= "(when-let
  [x
   (lookup m)

   y
   (lookup n)]

  (use x y))
"
           (format-source "(when-let [x (lookup m)
           y (lookup n)] (use x y))
")))))

(defn -main
  [& _]
  (let [{:keys [fail error]} (run-tests 'codestyle.zprint-test)]
    (when (pos? (+ fail error)) (System/exit 1))))
