(ns moon.core
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.walk :as walk]))

(defn edn-resource [path]
  (->> path
       io/resource
       slurp
       (edn/read-string {:readers {'edn/resource edn-resource}})
       (walk/postwalk (fn [form]
                        (if (and (symbol? form) (namespace form))
                          (let [avar (requiring-resolve form)]
                            (assert avar form)
                            avar)
                          form)))))
