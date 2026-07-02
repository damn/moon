(ns levelgen-test.start
  (:require [clojure.gdx.application :as application]
            [clojure.gdx.use-glfw-async :as use-glfw-async!]
            [clojure.edn-resource :refer [edn-resource]]))

(defn -main []
  (use-glfw-async!/f)
  (application/f! (edn-resource "config/levelgen-test.edn")))
