(ns gdx.scenes.scene2d.ui.dev-menu
  (:require [clojure.gdx :as gdx]
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
                                  (gdx/set-touchable! gdx/touchable-disabled))
                         :expand? true
                         :fill-x? true
                         :fill-y? true}]]})
    (gdx/table-set-fill-parent! true)))
