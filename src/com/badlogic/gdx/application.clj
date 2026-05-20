(ns com.badlogic.gdx.application
  (:require [clojure.config :refer [edn-resource]])
  (:import (com.badlogic.gdx ApplicationListener)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration))
  (:gen-class))

(def state (atom nil))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (let [{:keys [create
                dispose
                render
                resize
                title
                windowed-mode
                foreground-fps
                colors
                ]} (edn-resource "start.edn")]
    (Lwjgl3Application. (reify ApplicationListener
                          (create [_]
                            (reset! state
                                    (reduce (fn [ctx [f & params]]
                                              (apply f ctx params))
                                            {}
                                            create)))

                          (dispose [_]
                            (doseq [f dispose]
                              (f @state)))

                          (render [_]
                            (swap! state
                                   (fn [ctx]
                                     (reduce (fn [ctx [f & params]]
                                               (apply f ctx params))
                                             ctx
                                             render))))

                          (resize [_ width height]
                            (doseq [f resize]
                              (f @state width height)))

                          (pause [_])

                          (resume [_]))
                        (doto (Lwjgl3ApplicationConfiguration.)
                          (.setTitle title)
                          (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
                          (.setForegroundFPS foreground-fps)))))
