(ns moon.schema.enum
  (:require [clojure.edn :as edn]
            [moon.edn])
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox
                                               Skin)))

(defn malli-form [[_ & params] _schemas]
  (apply vector :enum params))

(defn create [schema v {:keys [ctx/skin]}]
  (doto (SelectBox. ^Skin skin)
    (.setItems ^"[Lcom.badlogic.gdx.scenes.scene2d.Actor;" (into-array (map moon.edn/->str (rest schema))))
    (.setSelected (moon.edn/->str v))))

(defn value [_  widget _schemas]
  (edn/read-string (SelectBox/.getSelected widget)))
