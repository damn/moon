(ns moon.application.create.app
  (:require moon.audio
            moon.graphics)
  (:import (com.badlogic.gdx Application
                             Gdx)
           (com.badlogic.gdx.graphics GL20)))

(defn step [ctx]
  (extend-type (class ctx)
    moon.audio/Audio
    (new-sound [{:keys [ctx/app]} path]
      (.newSound (Application/.getAudio app)
                 (.internal (Application/.getFiles app) path)))

    moon.graphics/Graphics
    (frames-per-second [{:keys [ctx/app]}]
      (.getFramesPerSecond (Application/.getGraphics app)))

    (delta-time [{:keys [ctx/app]}]
      (.getDeltaTime (Application/.getGraphics app)))

    (new-cursor [{:keys [ctx/app]} pixmap hotspot-x hotspot-y]
      (.newCursor (Application/.getGraphics app) pixmap hotspot-x hotspot-y))

    (set-cursor! [{:keys [ctx/app]} cursor]
      (.setCursor (Application/.getGraphics app) cursor))

    (clear! [{:keys [ctx/app]} r g b a]
      (.glClearColor (.getGL20 (Application/.getGraphics app)) r g b a)
      (.glClear      (.getGL20 (Application/.getGraphics app)) GL20/GL_COLOR_BUFFER_BIT))
    )
  (assoc ctx
         :ctx/app       Gdx/app
         :ctx/input     Gdx/input))
