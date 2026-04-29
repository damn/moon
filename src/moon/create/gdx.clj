(ns moon.create.gdx
  (:require [clojure.edn :as edn]
            [clojure.graphics :as graphics]
            [clojure.graphics.shape-drawer :as shape-drawer]
            [clojure.graphics.texture :as texture]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx Gdx)
           (com.badlogic.gdx.graphics Color
                                      Colors)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)
           (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn step [ctx]
  (doseq [[name [r g b a]]
          {
           "PRETTY_NAME" [0.84 0.8 0.52 1]
           }]
    (Colors/put name (Color. r g b a)))
  (set! (.initialTime (TooltipManager/getInstance)) 0)
  (merge ctx
         (let [batch (SpriteBatch.)
               shape-drawer-texture (graphics/white-pixel-texture Gdx/graphics)
               ]
           {:ctx/audio     (into {}
                                 ; pass this somehow as param to the whole create thingsie ?
                                 (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                                   [sound-name
                                    (.newSound Gdx/audio (.internal Gdx/files (format "sounds/%s.wav" sound-name)))]))
            :ctx/batch batch
            :ctx/files     Gdx/files
            :ctx/graphics  Gdx/graphics
            :ctx/input     Gdx/input
            :ctx/shape-drawer (shape-drawer/create batch (texture/region shape-drawer-texture 1 0 1 1))
            :ctx/shape-drawer-texture shape-drawer-texture
            })))
