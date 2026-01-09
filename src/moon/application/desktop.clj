(ns moon.application.desktop
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.walk :as walk]
            [moon.application :as application])
  (:import (com.badlogic.gdx ApplicationListener)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration))
  (:gen-class))

(def state (atom nil))

(defn start! [config]
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (let [dispose! (:dispose! config)
        render!  (:render!  config)
        resize!  (:resize!  config)
        listener (reify ApplicationListener
                   (create [_]
                     (reset! state (application/create! config)))

                   (dispose [_]
                     (dispose! @state))

                   (render [_]
                     (swap! state (fn [ctx]
                                    (reduce (fn [ctx [f & params]]
                                              (apply f ctx params))
                                            ctx
                                            render!))))

                   (resize [_ width height]
                     (resize! @state width height))

                   (pause [_])

                   (resume [_]))]
    (Lwjgl3Application. listener
                        (doto (Lwjgl3ApplicationConfiguration.)
                          (.setTitle (:title config))
                          (.setWindowedMode (:width (:window config))
                                            (:height (:window config)))
                          (.setForegroundFPS (:fps config))))))

(defn- edn-resource [path]
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

(defn -main []
  (start! (edn-resource "config.edn")))
