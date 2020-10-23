package org.openproject.camera.implementation.database

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table


@Table(name = "User")
open class SettingsTable: Model() {
    @Column(name = "username")
    open var ramMode = false

    @Column(name = "ip")
    open lateinit var ip: String
    @Column(name = "serverMode")
    open var serverMode = false

}