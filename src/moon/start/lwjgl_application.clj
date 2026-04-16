(ns moon.start.lwjgl-application
  (:require [clj.api.com.badlogic.gdx.application.listener :as listener]
            [clj.api.com.badlogic.gdx.backends.lwjgl3.application :as application]
            [clj.api.com.badlogic.gdx.backends.lwjgl3.application.config :as config]
            [clj.api.com.badlogic.gdx.gdx :as gdx]
            [clj.api.com.badlogic.gdx.graphics.color :as color]
            [clj.api.com.badlogic.gdx.graphics.colors :as colors]
            [clj.api.com.badlogic.gdx.graphics.texture.filter :as texture.filter]
            [clj.api.com.badlogic.gdx.graphics.g2d.freetype.font-generator :as font-generator]
            [clj.api.com.badlogic.gdx.graphics.g2d.freetype.font-generator.parameter :as parameter]
            [clj.api.com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [clj.api.com.badlogic.gdx.utils.disposable :as disposable]
            [gdl.bitmap-font :as font]
            [gdl.context :as context])
  (:require [qrecord.core :as q]))

(q/defrecord Context []
  context/SpriteBatch
  (sprite-batch [_]
    (sprite-batch/create))

  context/FreeType
  (generate-font [_ file-handle {:keys [size
                                        quality-scaling
                                        enable-markup?
                                        use-integer-positions?]}]
    (let [generator (font-generator/create file-handle)
          font (font-generator/generate-font generator
                                             (doto (parameter/create)
                                               (parameter/set-size! (* size quality-scaling))
                                               (parameter/set-min-filter! texture.filter/linear)
                                               (parameter/set-mag-filter! texture.filter/linear)))]
      (disposable/dispose! generator)
      (doto font
        (font/set-scale! (/ quality-scaling))
        (font/enable-markup! enable-markup?)
        (font/use-integer-positions! use-integer-positions?))))
  )

(defn step
  [{:keys [title
           window
           fps
           state
           create!
           dispose!
           render!
           resize!
           colors
           ]}]
  (doseq [[name rgba] colors]
    (colors/put! name (color/create rgba)))
  (let [state @state
        [create-fn create-params] create!
        [render-fn render-params] render!]
    (config/use-glfw-async!)
    (application/create (listener/create
                         {:create! (fn []
                                     (reset! state
                                             (create-fn (merge (map->Context {})
                                                               {:ctx/audio    (gdx/audio)
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
                        (doto (config/create)
                          (config/set-title! title)
                          (config/set-windowed-mode! window)
                          (config/set-foreground-fps! fps)))))
