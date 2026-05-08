(ns moon.application.create.app
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.gdx :as gdx]
            [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.gl20 :as gl20]
            moon.graphics))

(defn step [ctx]
  (assoc ctx
         :ctx/app       (gdx/app)
         :ctx/input     (gdx/input)))

(extend-type com.badlogic.gdx.Application
  moon.graphics/Graphics
  (frames-per-second [app]
    (graphics/frames-per-second (app/graphics app)))

  (delta-time [app]
    (graphics/delta-time (app/graphics app)))

  (set-cursor! [app cursor]
    (graphics/set-cursor! (app/graphics app) cursor))

  (clear! [app r g b a]
    (gl20/clear-color! (graphics/gl20 (app/graphics app)) r g b a)
    (gl20/clear!       (graphics/gl20 (app/graphics app)) gl20/color-buffer-bit)))
