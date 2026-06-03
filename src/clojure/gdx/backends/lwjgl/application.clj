(ns clojure.gdx.backends.lwjgl.application
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)))

(defn start!
  [{:keys [title windowed-mode foreground-fps]
    :as config}]
  (Lwjgl3Application. (let [state @(:state-var config)]
                        (reify ApplicationListener
                          (create [_]
                            (reset! state (reduce (fn [ctx [f & params]]
                                                    (apply f ctx params))
                                                  {:ctx/app Gdx/app}
                                                  (:create-pipeline config))))

                          (dispose [_]
                            ((:dispose! config) @state))

                          (render [_]
                            (swap! state (:render! config) (:render-params config)))

                          (resize [_ width height]
                            ((:resize! config) @state width height))

                          (pause [_])

                          (resume [_])))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle title)
                        (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
                        (.setForegroundFPS foreground-fps))))
