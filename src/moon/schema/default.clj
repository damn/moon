(ns moon.schema.default
  (:require [moon.actor :as actor]
            [moon.edn :as edn]
            [moon.string :as string]
            [moon.ui :as ui]))

(defn create
  [_ v {:keys [ctx/skin]}]
  (ui/create
   {:type :ui/label
    :text (string/truncate (edn/->str v) 60)
    :skin skin}))

(defn value
  [_  widget _schemas]
  ((actor/user-object widget) 1))
