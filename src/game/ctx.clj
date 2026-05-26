(ns game.ctx
  (:require [clojure.app :as app]
            [clojure.graphics :as graphics]
            [clojure.graphics.gl20 :as gl20]
            [clojure.input :as input]))

(defn delta-time
  [{:keys [ctx/app]}]
  (graphics/delta-time (app/graphics app)))

(defn frames-per-second
  [{:keys [ctx/app]}]
  (graphics/frames-per-second (app/graphics app)))

(defn clear-screen!
  [{:keys [ctx/app]}]
  (let [gl (graphics/gl20 (app/graphics app))]
    (gl20/clear-color! gl 0 0 0 0)
    (gl20/clear! gl gl20/color-buffer-bit)))

(defn set-cursor!
  [{:keys [ctx/app]} cursor]
  (graphics/set-cursor! (app/graphics app) cursor))

(defn key-pressed?
  [{:keys [ctx/app]} input-key]
  (input/key-pressed? (app/input app) input-key))

(defn key-just-pressed?
  [{:keys [ctx/app]} input-key]
  (input/key-just-pressed? (app/input app) input-key))

(defn mouse-position
  [{:keys [ctx/app]}]
  (input/mouse-position (app/input app)))

(defn button-just-pressed?
  [{:keys [ctx/app]} input-button]
  (input/button-just-pressed? (app/input app) input-button))
