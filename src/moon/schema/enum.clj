(ns moon.schema.enum
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.select-box :as select-box]
            [clojure.edn :as edn]
            [moon.edn]))

(defn malli-form [[_ & params] _schemas]
  (apply vector :enum params))

(defn create [schema v {:keys [ctx/skin]}]
  (doto (select-box/create skin)
    (select-box/set-items! (map moon.edn/->str (rest schema)))
    (select-box/set-selected! (moon.edn/->str v))))

(defn value [_  widget _schemas]
  (edn/read-string (select-box/selected widget)))
