(ns clojure.gdx
  (:require clojure.audio
            clojure.audio.sound
            clojure.files
            clojure.files.file-handle
            [clojure.gdx.graphics.pixmap :as pixmap]
            [clojure.gdx.graphics.pixmap.format :as pixmap.format]
            [clojure.gdx.input.buttons :as buttons]
            [clojure.gdx.input.keys :as keys]
            [clojure.gdx.utils.disposable :as disposable]
            clojure.graphics
            clojure.input
            )
  (:import (com.badlogic.gdx Audio
                             Files
                             Gdx
                             Graphics
                             Input
                             )
           (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics GL20)
           ))

(defn app []
  Gdx/app)

(defn audio []
  Gdx/audio)

(defn graphics []
  Gdx/graphics)

(defn files []
  Gdx/files)

(defn input []
  Gdx/input)

(extend-type Files
  clojure.files/Files
  (internal [this path]
    (.internal this path)))

(extend-type FileHandle
  clojure.files.file-handle/FileHandle
  (list [this]
    (.list this))

  (directory? [this]
    (.isDirectory this))

  (extension [this]
    (.extension this))

  (path [this]
    (.path this)))

(extend-type Input
  clojure.input/Input
  (key-pressed? [this key]
    (.isKeyPressed this (keys/k->value key)))

  (key-just-pressed? [this key]
    (.isKeyJustPressed this (keys/k->value key)))

  (button-just-pressed? [this button]
    (.isButtonJustPressed this (buttons/k->value button)))

  (mouse-position [this]
    [(.getX this)
     (.getY this)])

  (set-processor! [this input-processor]
    (.setInputProcessor this input-processor)))

(extend-type Audio
  clojure.audio/Audio
  (new-sound [this file-handle]
    (.newSound this file-handle)))

(extend-type Sound
  clojure.audio.sound/Sound
  (play! [this]
    (.play this)))

(extend-type Graphics
  clojure.graphics/Graphics
  (frames-per-second [graphics]
    (.getFramesPerSecond graphics))

  (delta-time [graphics]
    (.getDeltaTime graphics))

  (set-cursor! [graphics cursor]
    (.setCursor graphics cursor))

  (clear! [graphics r g b a]
    (.glClearColor (.getGL20 graphics) r g b a)
    (.glClear      (.getGL20 graphics) GL20/GL_COLOR_BUFFER_BIT))

  (new-cursor [graphics file-handle hotspot-x hotspot-y]
    (let [pixmap (pixmap/create file-handle)
          cursor (.newCursor graphics pixmap hotspot-x hotspot-y)]
      (disposable/dispose! pixmap)
      cursor))

  (white-pixel-texture [_graphics]
    (let [pixmap (doto (pixmap/create 1 1 pixmap.format/rgba8888)
                   (pixmap/set-color! 1 1 1 1)
                   (pixmap/draw-pixel! 0 0))
          texture (pixmap/texture pixmap)]
      (disposable/dispose! pixmap)
      texture)))
