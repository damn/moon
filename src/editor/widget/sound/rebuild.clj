(ns editor.widget.sound.rebuild
  (:require [clojure.gdx.scene2d.actor :refer [set-user-object!
                                               remove!]]
            [clojure.gdx.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [clojure.gdx.scene2d.group.clear-children :refer [clear-children!]]
            [clojure.gdx.scene2d.ui.widget-group.pack :refer [pack!]]
            [gdx.scenes.scene2d.ui :as ui]
            [clojure.gdx.scene2d.ui.table.add-rows :refer [add-rows!]])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn rebuild-sound-widget! [^Table table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (clear-children! table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (remove! (find-ancestor actor ui/window?))
    (pack! (find-ancestor table ui/window?))
    (let [[k _] (.getUserObject table)]
      (set-user-object! table [k sound-name]))))
