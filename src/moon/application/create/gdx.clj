(ns moon.application.create.gdx
  (:require [clojure.edn :as edn]
            [clojure.graphics.shape-drawer :as shape-drawer]
            [clojure.graphics.texture :as texture]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx Application)
           (com.badlogic.gdx.graphics Color
                                      Colors
                                      Pixmap
                                      Pixmap$Format
                                      Texture)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)
           (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn step [^Application app]

  (doseq [[name [r g b a]] {"PRETTY_NAME" [0.84 0.8 0.52 1]}]
    (Colors/put name (Color. r g b a)))

  (set! (.initialTime (TooltipManager/getInstance)) 0)

  (let [batch (SpriteBatch.)

        white-pixel-texture (let [pixmap (doto (Pixmap. 1 1 Pixmap$Format/RGBA8888)
                                            (.setColor 1 1 1 1)
                                            (.drawPixel 0 0))
                                   texture (Texture. pixmap)]
                               (.dispose pixmap)
                               texture)
        ]
    {:ctx/audio (into {}
                      (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                        [sound-name
                         (.newSound (.getAudio app) (.internal (.getFiles app) (format "sounds/%s.wav" sound-name)))]))

     :ctx/batch batch

     :ctx/files     (.getFiles app)

     :ctx/graphics  (.getGraphics app)

     :ctx/input     (.getInput app)

     :ctx/shape-drawer (shape-drawer/create batch (texture/region white-pixel-texture 1 0 1 1))

     :ctx/shape-drawer-texture white-pixel-texture}))
