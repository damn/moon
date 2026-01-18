(ns moon.schema.default
  (:require [moon.utils :as utils])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(defn create
  [_ v {:keys [^Skin ctx/skin]}]
  (Label. (utils/truncate (utils/->edn-str v) 60) skin))

(defn value
  [_  widget _schemas]
  ((Actor/.getUserObject widget) 1))
