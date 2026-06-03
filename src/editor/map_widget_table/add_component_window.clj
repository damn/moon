(ns editor.map-widget-table.add-component-window
  (:require [clojure.gdx.scene2d.event :as event]
            [editor.map-widget-table.component-row :as component-row]
            [editor.rebuild :as rebuild]
            [editor.widget :as widget]
            [clojure.gdx.scene2d.actor :refer [remove!]]
            [clojure.gdx.scene2d.ui.table.add-rows :refer [add-rows!]]
            [clojure.gdx.scene2d.ui.text-button :as text-button]
            [clojure.gdx.scene2d.ui.widget-group.pack :refer [pack!]]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [gdx.scenes.scene2d.ui.window :as window]
            [moon.schemas :as schemas]))

(defn f
  [{:keys [schemas schema map-widget-table skin]}]
  (let [window (window/create
                {:title "Choose"
                 :skin skin
                 :window/close-button? skin
                 :window/modal? true
                 :table/cell-defaults {:pad 5}})
        remaining-ks (sort (remove (set (keys (widget/value schema map-widget-table schemas)))
                                   (schemas/map-keys schemas schema)))]
    (add-rows!
     window
     (for [k remaining-ks]
       [{:actor (text-button/create
                 {:skin skin
                  :text (name k)
                  :actor/listeners [(change-listener/create
                                     (fn [event _actor]
                                       (remove! window)
                                       (let [ctx (:stage/ctx (event/stage event))]
                                         (add-rows! map-widget-table [(component-row/create
                                                                       {:skin skin
                                                                        :editor-widget (widget/build ctx
                                                                                                     (get schemas k)
                                                                                                     k
                                                                                                     (schemas/default-value schemas k))
                                                                        :k k
                                                                        :display-remove-component-button? (schemas/optional? schemas schema k)
                                                                        :table map-widget-table})])
                                         (rebuild/f! ctx))))]})}]))
    (pack! window)
    window))
