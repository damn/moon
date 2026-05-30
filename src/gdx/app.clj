; TODO combinationof gdx -> new name -> new abstraction layer !?
; => First layer: one class - one namespace - same class namespace name ... 'badlogic' again !
; maybe not 'com.badlogic.gdx' because needs to be greppable / replaceable
; 'goodlogic'
(ns gdx.app
  (:require [com.badlogic.gdx.audio :as audio]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]
            [gdx.application :as app]
            [gdx.files :as files]
            [gdx.graphics :as graphics]
            [gdx.input :as input])
  (:import (com.badlogic.gdx.graphics Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn new-sound [app path]
  (audio/new-sound (app/audio app)
                   (files/internal (app/files app) path)))

(defn new-cursor [app path [hotspot-x hotspot-y]]
  (let [pixmap (pixmap/create (files/internal (app/files app) path))
        cursor (graphics/new-cursor (app/graphics app) pixmap hotspot-x hotspot-y)]
    (pixmap/dispose! pixmap)
    cursor))

(defn new-font
  [app
   {:keys [path
           size
           quality-scaling]}]
  (let [generator (FreeTypeFontGenerator. (files/internal (app/files app) path))
        font (.generateFont generator
                            (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                              (set! (.size params) (* size quality-scaling))
                              ; texture.filter/linear because scaling to world-units
                              (set! (.minFilter params) Texture$TextureFilter/Linear)
                              (set! (.magFilter params) Texture$TextureFilter/Linear)
                              params))]
    (.dispose generator)
    (.setScale (.getData font) (/ quality-scaling))
    (set! (.markupEnabled (.getData font)) true)
    (.setUseIntegerPositions font false)
    font))

(defn skin [app path]
  (let [skin (Skin. (files/internal (app/files app) path))]
    (set! (.markupEnabled (-> skin
                              (.getFont "default-font")
                              .getData))
          true)
    skin))

(defn set-input-processor! [app input-processor]
  (input/set-processor! (app/input app) input-processor))

(defn key-pressed? [app input-key]
  (input/key-pressed? (app/input app) input-key))

(defn mouse-position [app]
  (input/mouse-position (app/input app)))

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
