(ns moon.schema.val-max
  (:require [clojure.edn :as edn]
            [moon.schema :as schema]
            [moon.utils :as utils]
            [moon.val-max :as val-max])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextField
                                               TextTooltip)))

(defmethod schema/malli-form :s/val-max [_ _schemas]
  val-max/schema)

(defmethod schema/create :s/val-amx
  [schema v {:keys [^Skin ctx/skin]}]
  (doto (TextField. (utils/->edn-str v) skin)
    (.addListener (TextTooltip. (str schema) skin))))

(defmethod schema/value :s/val-max
  [_  widget _schemas]
  (edn/read-string (TextField/.getText widget)))
