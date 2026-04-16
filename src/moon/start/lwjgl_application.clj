(ns moon.start.lwjgl-application
  (:require [clj.api.com.badlogic.gdx.gdx :as gdx]
            [clojure.gdx.backends.lwjgl :as lwjgl]
            [clojure.gdx.colors :as colors])
  (:require [qrecord.core :as q]))

(q/defrecord Context [])

(defn step
  [{:keys [config
           state
           create!
           dispose!
           render!
           resize!
           colors
           ]}]
  (colors/put! colors)
  (lwjgl/use-glfw-async!)
  (lwjgl/application! (let [state @state
                            [create-fn create-params] create!
                            [render-fn render-params] render!]
                        {:create! (fn []
                                    (reset! state
                                            (create-fn (merge (map->Context {})
                                                              {:ctx/app (gdx/app)
                                                               :ctx/audio    (gdx/audio)
                                                               :ctx/graphics (gdx/graphics)
                                                               :ctx/files    (gdx/files)
                                                               :ctx/input    (gdx/input)})
                                                       create-params)))
                         :dispose! (fn []
                                     (dispose! @state))
                         :render! (fn []
                                    (swap! state render-fn render-params))
                         :resize! (fn [width height]
                                    (resize! @state width height))
                         :pause! (fn [])
                         :resume! (fn [])})
                      config))
