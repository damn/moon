(ns editor.map-widget-table.component-row
  (:require [clojure.gdx :as gdx]
            [editor.map-widget-table.k-label-text :as k-label-text]
            [scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.table :as table]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Event)))

(defn create
  [{:keys [skin
           editor-widget
           display-remove-component-button?
           k
           table]}]
  [{:actor (table/create
            {:table/cell-defaults {:pad 2}
             :table/rows [[{:actor (when display-remove-component-button?
                                     (doto (text-button/create
                                            {:text "-"
                                             :skin skin})
                                       (Actor/.addListener (change-listener/create
                                                            (fn [event _actor]
                                                              (Actor/.remove (first (filter (fn [actor]
                                                                                              (and (Actor/.getUserObject actor)
                                                                                                   (= k ((Actor/.getUserObject actor) 0))))
                                                                                            (gdx/get-children table))))
                                                              (let [ctx (:stage/ctx (Event/.getStage event))]
                                                                ((:ctx/rebuild-editor-window! ctx) ctx)))))))
                            :left? true}
                           {:actor (label/create
                                    {:text (k-label-text/f k)
                                     :skin skin})}]]})
    :right? true}
   {:actor nil #_(com.kotcrab.vis.ui.widget.Separator. "vertical")
    :pad-top 2
    :pad-bottom 2
    :fill-y? true
    :expand-y? true}
   {:actor editor-widget
    :left? true}])
