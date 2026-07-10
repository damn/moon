(ns clojure.ui.dev-menu
  (:require
            
            [clojure.table-set-opts :as table-set-opts]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [clojure.ui.dev-menu.main-table :as main-table]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]))

(defn create
  [{:keys [menus update-labels skin]}]
  (doto (doto (table/new)
    (table-set-opts/set-opts! {:table/rows [[{:actor (main-table/f skin menus update-labels)
                         :expand-x? true
                         :fill-x? true
                         :colspan 1}]
                       [{:actor (doto (label/new "" skin)
                                  (actor/setTouchable touchable/disabled))
                         :expand? true
                         :fill-x? true
                         :fill-y? true}]]}))
    (layout/setFillParent true)))
