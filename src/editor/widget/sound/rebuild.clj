(ns editor.widget.sound.rebuild
  (:require [clojure.gdx.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [clojure.gdx.scene2d.actor.user-object :refer [actor-user-object]]
            [clojure.gdx.scene2d.actor.remove :refer [remove!]]
            [clojure.gdx.scene2d.actor.set-user-object :refer [set-user-object!]]
            [clojure.gdx.scene2d.group.clear-children :refer [clear-children!]]
            [gdx.scenes.scene2d.ui :as ui]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.widget-group :as widget-group]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (clear-children! table)
    (table/add-rows! table [(->sound-columns skin table sound-name)])
    (remove! (find-ancestor actor ui/window?))
    (widget-group/pack! (find-ancestor table ui/window?))
    (let [[k _] (actor-user-object table)]
      (set-user-object! table [k sound-name]))))
