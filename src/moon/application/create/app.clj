(ns moon.application.create.app
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.gdx :as gdx]
            [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.gl20 :as gl20]
            moon.graphics))

(defn step [ctx]
  (extend-type (class ctx)
    moon.graphics/Graphics
    (frames-per-second [{:keys [ctx/app]}]
      (graphics/frames-per-second (app/graphics app)))

    (delta-time [{:keys [ctx/app]}]
      (graphics/delta-time (app/graphics app)))

    (new-cursor [{:keys [ctx/app]} pixmap hotspot-x hotspot-y]
      (graphics/new-cursor (app/graphics app) pixmap hotspot-x hotspot-y))

    (set-cursor! [{:keys [ctx/app]} cursor]
      (graphics/set-cursor! (app/graphics app) cursor))

    (clear! [{:keys [ctx/app]} r g b a]
      (gl20/clear-color! (graphics/gl20 (app/graphics app)) r g b a)
      (gl20/clear!       (graphics/gl20 (app/graphics app)) gl20/color-buffer-bit))
    )
  (assoc ctx
         :ctx/app       (gdx/app)
         :ctx/input     (gdx/input)))
