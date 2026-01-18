(ns moon.schema.val-max
  (:require [clojure.edn :as edn]
            [moon.utils :as utils]
            [moon.val-max :as val-max])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextField
                                               TextTooltip)))

(defn malli-form [_ _schemas]
  val-max/schema)

(defn create
  [schema v {:keys [^Skin ctx/skin]}]
  (doto (TextField. (utils/->edn-str v) skin)
    (.addListener (TextTooltip. (str schema) skin))))

(defn value
  [_  widget _schemas]
  (edn/read-string (TextField/.getText widget)))
