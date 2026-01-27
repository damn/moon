(ns moon.schema.default
  (:require [moon.edn :as edn]
            [moon.string :as string]
            [moon.ui :as ui])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn create
  [_ v {:keys [ctx/skin]}]
  (ui/actor
   {:type :ui/label
    :label/text (string/truncate (edn/->str v) 60)
    :label/skin skin}))

(defn value
  [_  widget _schemas]
  ((Actor/.getUserObject widget) 1))
