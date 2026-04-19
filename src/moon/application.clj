(ns moon.application
  (:require [clojure.gdx.backends.lwjgl :as lwjgl]
            [clojure.gdx.backends.lwjgl.config :as config]
            [clojure.gdx.scene2d.ui.tooltip-manager :as tooltip-manager])
  (:import (com.badlogic.gdx ApplicationListener)))

(def state (atom nil))

(defn start! [{:keys [listener config]}]
  (config/use-glfw-async!)
  (lwjgl/application! (let [{:keys [create!
                                    create-params
                                    dispose!
                                    render!
                                    render-params
                                    resize!]}
                            listener]
                        (reify ApplicationListener
                          (create [_]
                            (tooltip-manager/set-initial-time! 0)
                            (reset! state (create! create-params)))

                          (dispose [_]
                            (dispose! @state))

                          (render [_]
                            (swap! state render! render-params))

                          (resize [_ width height]
                            (resize! @state width height))

                          (pause [_])

                          (resume [_])))
                      (config/create config)))
