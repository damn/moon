(ns moon.schema.default
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [moon.actor :as actor]
            [moon.edn :as edn]
            [moon.string :as string]))

(defn create
  [_ v {:keys [ctx/skin]}]
  (label/create (string/truncate (edn/->str v) 60) skin))

(defn value
  [_  widget _schemas]
  ((actor/user-object widget) 1))
