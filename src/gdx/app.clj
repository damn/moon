(ns gdx.app
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.files :as files]
            [gdx.graphics :as graphics]
            [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.data :as font.data]
            [com.badlogic.gdx.graphics.g2d.freetype.freetype-font-generator :as font-generator]
            [com.badlogic.gdx.graphics.g2d.freetype.freetype-font-generator.parameter :as parameter]
            [com.badlogic.gdx.graphics.texture.texture-filter :as texture-filter]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]))

(defn new-font
  [app
   {:keys [path
           size
           quality-scaling]}]
  (let [generator (font-generator/create (files/internal (app/files app) path))
        font (font-generator/generate-font generator
                                           (parameter/create
                                            {:size (* size quality-scaling)
                                             ; texture.filter/linear because scaling to world-units
                                             :min-filter texture-filter/linear
                                             :mag-filter texture-filter/linear}))]
    (font-generator/dispose! generator)
    (font.data/set-scale! (font/data font) (/ quality-scaling))
    (font.data/enable-markup! (font/data font))
    (font/set-use-integer-positions! font false)
    font))

(defn skin [app path]
  (let [skin (skin/create (files/internal (app/files app) path))]
    (-> skin
        (skin/font "default-font")
        font/data
        font.data/enable-markup!)
    skin))

(defn set-input-processor! [app input-processor]
  (input/set-processor! (app/input app) input-processor))

(defn key-pressed? [app input-key]
  (input/key-pressed? (app/input app) input-key))

(defn mouse-position [app]
  [(input/x (app/input app))
   (input/y (app/input app))])

(defn button-just-pressed? [app input-button]
  (input/button-just-pressed? (app/input app) input-button))

(defn key-just-pressed? [app input-key]
  (input/key-just-pressed? (app/input app) input-key))

(defn clear-screen! [app r g b a]
  (graphics/clear! (app/graphics app) r g b a))

(defn delta-time [app]
  (graphics/delta-time (app/graphics app)))

(defn set-cursor! [app cursor]
  (graphics/set-cursor! (app/graphics app) cursor))

(defn frames-per-second [app]
  (graphics/frames-per-second (app/graphics app)))
