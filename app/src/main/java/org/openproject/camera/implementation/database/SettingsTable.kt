package org.openproject.camera.implementation.database

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table


@Table(name = "User")
open class SettingsTable: Model() {
    @Column(name = "username")
    public lateinit var ramMode: String

    @Column(name = "ip")
    public lateinit var ip: String

}