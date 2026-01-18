(ns moon.schema.enum
  (:require [clojure.edn :as edn]
            [moon.utils :as utils])
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox
                                               Skin)))

(defn malli-form [[_ & params] _schemas]
  (apply vector :enum params))

(defn create [schema v {:keys [ctx/skin]}]
  (doto (SelectBox. ^Skin skin)
    (.setItems ^"[Lcom.badlogic.gdx.scenes.scene2d.Actor;" (into-array (map utils/->edn-str (rest schema))))
    (.setSelected (utils/->edn-str v))))

(defn value [_  widget _schemas]
  (edn/read-string (SelectBox/.getSelected widget)))
