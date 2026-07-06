(ns gdx.scenes.scene2d.ui.dev-menu
  (:require [clojure.gdx.actor.set-touchable :as set-touchable]
            [clojure.gdx.layout.set-fill-parent :as set-fill-parent]
            [com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [gdx.scenes.scene2d.ui.dev-menu.main-table :as main-table]
            [scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.table :as table]))

(defn create
  [{:keys [menus update-labels skin]}]
  (doto (table/create
         {:table/rows [[{:actor (main-table/f skin menus update-labels)
                         :expand-x? true
                         :fill-x? true
                         :colspan 1}]
                       [{:actor (doto (label/create
                                       {:text ""
                                        :skin skin})
                                  (set-touchable/f touchable/disabled))
                         :expand? true
                         :fill-x? true
                         :fill-y? true}]]})
    (set-fill-parent/f true)))
