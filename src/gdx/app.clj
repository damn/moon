(ns gdx.app
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.graphics :as graphics]))

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

(defn delta-time [app]
  (graphics/delta-time (app/graphics app)))

(defn set-cursor! [app cursor]
  (graphics/set-cursor! (app/graphics app) cursor))

(defn frames-per-second [app]
  (graphics/frames-per-second (app/graphics app)))
