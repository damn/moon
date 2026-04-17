(ns moon.application
  (:require [clojure.gdx.backends.lwjgl :as lwjgl]
            [clojure.gdx.backends.lwjgl.config :as config]
            [clojure.gdx.colors :as colors])
  (:import (com.badlogic.gdx ApplicationListener)))

(def state (atom nil))

(defn start! [{:keys [colors listener config]}]
  (colors/put! colors)
  (config/use-glfw-async!)
  (lwjgl/application! (let [{:keys [create!
                                    dispose!
                                    render!
                                    resize!]} listener]
                        (reify ApplicationListener
                          (create [_]
                            (reset! state (let [[create-fn create-params] create!]
                                            (create-fn create-params))))

                          (dispose [_]
                            (dispose! @state))

                          (render [_]
                            (let [[render-fn render-params] render!]
                              (swap! state render-fn render-params)))

                          (resize [_ width height]
                            (resize! @state width height))

                          (pause [_])

                          (resume [_])))
                      (config/create config)))
