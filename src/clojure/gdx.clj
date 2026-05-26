(ns clojure.gdx
  (:require [clojure.edn :as edn]
            [clojure.gdx.backends.lwjgl3.application :as application]
            [clojure.gdx.backends.lwjgl3.application-configuration :as config]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx ApplicationListener)))

(defn create-listener
  [{:keys [create!
           dispose!
           render!
           resize!
           pause!
           resume!]}]
  (reify ApplicationListener
    (create [_]
      (create!))

    (dispose [_]
      (dispose!))

    (render [_]
      (render!))

    (resize [_ width height]
      (resize! width height))

    (pause [_]
      (pause!))

    (resume [_]
      (resume!))))

(defn application! [config]
  (->> "clojure.gdx.edn"
       io/resource
       slurp
       edn/read-string
       (run! require))
  (config/use-glfw-async!)
  (application/create (create-listener config)
                      (config/create config)))
