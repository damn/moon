(ns clojure.editor.create-widget-add-component-window
  (:require 
            [clojure.table-set-opts :as table-set-opts]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.ui.window.add-close-button :as add-close-button]
            [clojure.ui.table.add-rows :refer [add-rows!]]
            [clojure.schemas :as schemas]
            [clojure.editor.create-widget-build-widget :as build-widget]
            [clojure.editor.create-widget-create-component-row :as create-component-row]
            [clojure.editor.create-widget-rebuild-editor-window :as rebuild-editor-window]
            [clojure.editor.widget-value :refer [widget-value]]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [clojure.optional :refer [optional?]]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [clojure.schemas-map-keys :refer [map-keys]]
            [clojure.set :as set]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]))

(defn add-component-window
  [{:keys [schemas schema map-widget-table skin]}]
  (let [window (doto (doto (window/new "Choose" skin)
    (table-set-opts/set-opts! {:title "Choose"
                       :skin skin
                       :table/cell-defaults {:pad 5}}))
                 (add-close-button/f! skin)
                 (gdx-window/setModal true))
        remaining-ks (sort (remove (set (keys (widget-value schema map-widget-table schemas)))
                                   (map-keys schemas schema)))]
    (add-rows!
     window
     (for [k remaining-ks]
       [{:actor (doto (text-button/new (name k) skin)
                  (actor/addListener (change-listener/create
                                           (fn [event _actor]
                                             (actor/remove window)
                                             (let [ctx (:stage/ctx (event/getStage event))]
                                               (add-rows! map-widget-table [(create-component-row/create-component-row
                                                                            {:skin skin
                                                                             :editor-widget (build-widget/build-widget ctx
                                                                                                                          (get schemas k)
                                                                                                                          k
                                                                                                                          (schemas/default-value schemas k))
                                                                             :k k
                                                                             :display-remove-component-button? (optional? schemas schema k)
                                                                             :table map-widget-table})])
                                               (rebuild-editor-window/rebuild-editor-window! ctx))))))}]))
    (layout/pack window)
    window))
