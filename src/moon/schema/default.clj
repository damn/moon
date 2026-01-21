(ns moon.schema.default
  (:require [moon.ui :as ui]
            [moon.utils :as utils])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn create
  [_ v {:keys [ctx/skin]}]
  (ui/actor
   {:type :ui/label
    :label/text (utils/truncate (utils/->edn-str v) 60)
    :label/skin skin}))

(defn value
  [_  widget _schemas]
  ((Actor/.getUserObject widget) 1))
