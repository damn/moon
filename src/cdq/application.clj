(ns cdq.application
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration))
  (:gen-class))

(defn- edn-resource [path]
  (->> path
       io/resource
       slurp
       (edn/read-string {:readers {'edn/resource edn-resource}})))

(def state (atom nil))

(defn -main []
  (let [config (edn-resource "config.edn")
        create!  (requiring-resolve (:create!  config))
        dispose! (requiring-resolve (:dispose! config))
        render!  (map requiring-resolve (:render!  config))
        resize!  (requiring-resolve (:resize!  config))]
    (Lwjgl3ApplicationConfiguration/useGlfwAsync)
    (Lwjgl3Application. (reify ApplicationListener
                          (create [_]
                            (reset! state (create! {:ctx/app Gdx/app} config)))

                          (dispose [_]
                            (dispose! @state))

                          (render [_]
                            (swap! state (fn [ctx]
                                           (reduce (fn [ctx f]
                                                     (f ctx))
                                                   ctx
                                                   render!))))

                          (resize [_ width height]
                            (resize! @state width height))

                          (pause [_])

                          (resume [_]))
                        (doto (Lwjgl3ApplicationConfiguration.)
                          (.setTitle (:title config))
                          (.setWindowedMode (:width (:window config))
                                            (:height (:window config)))
                          (.setForegroundFPS (:fps config))))))
