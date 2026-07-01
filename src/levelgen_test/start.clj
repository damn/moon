(ns levelgen-test.start
  (:require [moon.application.start]
            [clojure.edn-resource :refer [edn-resource]]))

(defn -main []
  (moon.application.start/f! (edn-resource "config/levelgen-test.edn")))
