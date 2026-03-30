(ns moon.schema.val-max
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [clojure.edn :as edn]
            [moon.actor :as actor]
            [moon.edn]
            [moon.val-max :as val-max]))

(defn malli-form [_ _schemas]
  val-max/schema)

(defn create
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (moon.edn/->str v) skin)
    (actor/add-listener! (text-tooltip/create (str schema) skin))))

(defn value
  [_  widget _schemas]
  (edn/read-string (text-field/text widget)))
