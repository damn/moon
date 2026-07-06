(ns gdx.scene2d.ui.dev-menu
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [gdx.scene2d.ui.dev-menu.main-table :as main-table]
            [gdx.scene2d.ui.label :as label]
            [gdx.scene2d.ui.table :as table]))

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
                                  (actor/set-touchable! touchable/disabled))
                         :expand? true
                         :fill-x? true
                         :fill-y? true}]]})
    (layout/set-fill-parent! true)))
