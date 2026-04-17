(ns moon.schema.default
  (:require [clojure.scene2d.actor :as actor]
            [moon.edn :as edn]
            [moon.string :as string]))

(defn create
  [_ v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/label
    :text (string/truncate (edn/->str v) 60)
    :skin skin}))

(defn value
  [_  widget _schemas]
  ((actor/user-object widget) 1))
