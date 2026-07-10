(ns clojure.editor.create-widget-create-component-row
  (:require 
            [clojure.table-set-opts :as table-set-opts]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.editor.create-widget-rebuild-editor-window :as rebuild-editor-window]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [clojure.k-label-text :as k-label-text]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]))

(defn create-component-row
  [{:keys [skin
           editor-widget
           display-remove-component-button?
           k
           table]}]
  [{:actor (doto (table/new)
    (table-set-opts/set-opts! {:table/cell-defaults {:pad 2}
             :table/rows [[{:actor (when display-remove-component-button?
                                     (doto (text-button/new "-" skin)
                                       (actor/addListener (change-listener/create
                                                                (fn [event _actor]
                                                                  (actor/remove (first (filter (fn [actor]
                                                                                                          (and (actor/getUserObject actor)
                                                                                                               (= k ((actor/getUserObject actor) 0))))
                                                                                                        (group/getChildren table))))
                                                                  (let [ctx (:stage/ctx (event/getStage event))]
                                                                    (rebuild-editor-window/rebuild-editor-window! ctx)))))))
                            :left? true}
                           {:actor (label/new (k-label-text/f k) skin)}]]}))
    :right? true}
   {:actor nil
    :pad-top 2
    :pad-bottom 2
    :fill-y? true
    :expand-y? true}
   {:actor editor-widget
    :left? true}])
