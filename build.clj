(ns build
  (:require
   [clojure.tools.build.api :as b]))

(def lib 'io.github.enajski/codestyle)
(def default-version "1.0.0")
(def class-dir "target/classes")

(defn- version
  [params]
  (or (:app-version params) default-version))

(defn- jar-file
  [params]
  (format "target/%s-%s.jar" (name lib) (version params)))

(defn- basis
  []
  (b/create-basis {:project "deps.edn"}))

(defn clean
  [_]
  (b/delete {:path "target"}))

(defn jar
  [params]
  (clean nil)
  (let [v (version params)
        b (basis)]
    (b/write-pom {:class-dir class-dir
                  :lib lib
                  :version v
                  :basis b
                  :src-dirs ["src"]
                  :resource-dirs ["resources"]})
    (b/copy-dir {:src-dirs ["src" "resources"]
                 :target-dir class-dir})
    (b/jar {:class-dir class-dir
            :jar-file (jar-file params)})
    (println "Built" (jar-file params))))

(defn install
  [params]
  (jar params)
  (b/install {:basis (basis)
              :lib lib
              :version (version params)
              :jar-file (jar-file params)
              :class-dir class-dir})
  (println "Installed" lib (version params) "to local ~/.m2"))
