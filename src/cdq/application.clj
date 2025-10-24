(ns cdq.application
  (:require [cdq.ctx :as ctx]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [qrecord.core :as q])
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
  (let [config (edn-resource "config.edn")]
    (Lwjgl3ApplicationConfiguration/useGlfwAsync)
    (Lwjgl3Application. (reify ApplicationListener
                          (create [_]
                            (reset! state (ctx/create! {:audio    Gdx/audio
                                                        :files    Gdx/files
                                                        :graphics Gdx/graphics
                                                        :input    Gdx/input}
                                                       config)))

                          (dispose [_]
                            (ctx/dispose! @state))

                          (render [_]
                            (swap! state ctx/render!))

                          (resize [_ width height]
                            (ctx/resize! @state width height))

                          (pause [_])

                          (resume [_]))
                        (doto (Lwjgl3ApplicationConfiguration.)
                          (.setTitle (:title config))
                          (.setWindowedMode (:width (:window config))
                                            (:height (:window config)))
                          (.setForegroundFPS (:fps config))))))
