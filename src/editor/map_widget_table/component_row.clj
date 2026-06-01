(ns editor.map-widget-table.component-row
  (:require [clojure.gdx.scene2d.event :as event]
            [clojure.gdx.scene2d.actor.remove :refer [remove!]]
            [clojure.gdx.scene2d.actor.user-object :refer [actor-user-object]]
            [editor.map-widget-table.k-label-text :as k-label-text]
            [editor.rebuild :as rebuild]
            [clojure.gdx.scene2d.group.children :refer [children]]
            [gdx.scenes.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.text-button :as text-button]))

(defn create
  [{:keys [skin
           editor-widget
           display-remove-component-button?
           k
           table]}]
  [{:actor (table/create
            {:table/cell-defaults {:pad 2}
             :table/rows [[{:actor (when display-remove-component-button?
                                     (text-button/create
                                      {:text "-"
                                       :skin skin
                                       :actor/listeners {:listener/change
                                                         (fn [event _actor]
                                                           (remove! (first (filter (fn [actor]
                                                                                     (and (actor-user-object actor)
                                                                                          (= k ((actor-user-object actor) 0))))
                                                                                   (children table))))
                                                           (rebuild/f! (:stage/ctx (event/stage event))))}}))
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
