(ns moon.schema.enum
  (:require [clojure.edn :as edn]
            [moon.schema :as schema]
            [moon.utils :as utils])
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox
                                               Skin)))

(defmethod schema/malli-form :s/enum [[_ & params] _schemas]
  (apply vector :enum params))

(defmethod schema/create :s/enum [schema v {:keys [ctx/skin]}]
  (doto (SelectBox. ^Skin skin)
    (.setItems ^"[Lcom.badlogic.gdx.scenes.scene2d.Actor;" (into-array (map utils/->edn-str (rest schema))))
    (.setSelected (utils/->edn-str v))))

(defmethod schema/value :s/enum [_  widget _schemas]
  (edn/read-string (SelectBox/.getSelected widget)))
