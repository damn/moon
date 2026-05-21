(ns game.application
  (:require [clojure.config :refer [edn-resource]]
            [clojure.impl])
  (:import (com.badlogic.gdx Gdx
                             ApplicationListener)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration))
  (:gen-class))

(def state (atom nil))

(defn -main []
  (let [{:keys [requires
                create
                dispose!
                render
                resize!
                title
                windowed-mode
                foreground-fps]} (edn-resource "start.edn")]
    (run! require requires)
    (clojure.impl/load!)
    (Lwjgl3ApplicationConfiguration/useGlfwAsync)
    (Lwjgl3Application. (reify ApplicationListener
                          (create [_]
                            (reset! state
                                    (reduce (fn [ctx [f & params]]
                                              (apply f ctx params))
                                            Gdx/app
                                            create)))

                          (dispose [_]
                            (dispose! @state))

                          (render [_]
                            (swap! state
                                   (fn [ctx]
                                     (reduce (fn [ctx [f & params]]
                                               (apply f ctx params))
                                             ctx
                                             render))))

                          (resize [_ width height]
                            (resize! @state width height))

                          (pause [_])

                          (resume [_]))
                        (doto (Lwjgl3ApplicationConfiguration.)
                          (.setTitle title)
                          (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
                          (.setForegroundFPS foreground-fps)))))
