(ns clojure.gdx
  (:require [clojure.edn :as edn]
            [clojure.gdx.application-listener :as listener]
            [clojure.gdx.backends.lwjgl3.application :as application]
            [clojure.gdx.backends.lwjgl3.application-configuration :as config]
            [clojure.java.io :as io]))

(defn application! [config]
  (->> "clojure.gdx.edn"
       io/resource
       slurp
       edn/read-string
       (run! require))
  (config/use-glfw-async!)
  (application/create (listener/create config)
                      (config/create config)))
