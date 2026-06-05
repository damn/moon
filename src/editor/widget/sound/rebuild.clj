(ns editor.widget.sound.rebuild
  (:require [clojure.scene2d.actor.get-user-object :refer [get-user-object]]
            [clojure.scene2d.actor.remove :refer [remove!]]
            [clojure.scene2d.actor.set-user-object :refer [set-user-object!]]
            [clojure.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [clojure.scene2d.group.clear-children :refer [clear-children!]]
            [clojure.scene2d.ui.widget-group.pack :refer [pack!]]
            [gdx.scenes.scene2d.ui :as ui]
            [clojure.scene2d.ui.table.add-rows :refer [add-rows!]])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn rebuild-sound-widget! [^Table table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (clear-children! table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (remove! (find-ancestor actor ui/window?))
    (pack! (find-ancestor table ui/window?))
    (let [[k _] (get-user-object table)]
      (set-user-object! table [k sound-name]))))
