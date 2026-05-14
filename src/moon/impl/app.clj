(ns moon.impl.app
  (:require [moon.app :as app])
  (:import (com.badlogic.gdx Application
                             Gdx)
           (com.badlogic.gdx.graphics GL20)))

(defn create [_ctx]
  Gdx/app)

(extend-type Application
  app/App
  (new-sound [app path]
    (.newSound (.getAudio app)
               (.internal (.getFiles app) path)))

  (frames-per-second [app]
    (.getFramesPerSecond (.getGraphics app)))

  (delta-time [app]
    (.getDeltaTime (.getGraphics app)))

  (new-cursor [app pixmap hotspot-x hotspot-y]
    (.newCursor (.getGraphics app) pixmap hotspot-x hotspot-y))

  (set-cursor! [app cursor]
    (.setCursor (.getGraphics app) cursor))

  (clear! [app r g b a]
    (.glClearColor (.getGL20 (.getGraphics app)) r g b a)
    (.glClear      (.getGL20 (.getGraphics app)) GL20/GL_COLOR_BUFFER_BIT))

  (set-input-processor! [this input-processor]
    (.setInputProcessor (.getInput this) input-processor))

  (key-pressed? [this key]
    (.isKeyPressed (.getInput this) key))

  (key-just-pressed? [this key]
    (.isKeyJustPressed (.getInput this) key))

  (button-just-pressed? [this button]
    (.isButtonJustPressed (.getInput this) button))

  (mouse-position [this]
    [(.getX (.getInput this))
     (.getY (.getInput this))]))
